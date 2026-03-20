#!/usr/bin/perl
# ==============================================================================================
# = Pontificia Universidad Javeriana =
#
# Autores:         Juan Diego Ariza, Nicolas Leon, Juan Diego Pardo, Juan Sebastian Urbano
# Curso:           Sistemas Distribuidos - 2026-10
# Fecha:           Marzo 2026
#
# Archivo:         lanzador.pl
# Descripcion:
#   Script que automatiza la bateria de experimentos del Taller . 
#   Ejecuta los dos algoritmos (FxT y FxC) con diferentes
#   configuraciones de tamano de matriz, procesos MPI e hilos OpenMP.
#   Cada configuracion se corre N_REPS veces y los tiempos se guardan en CSV.
#
# Uso:
#   perl lanzador.pl
#
# Salida:
#   resultados_fxt.csv  -> Tiempos del algoritmo Filas x Transpuesta
#   resultados_fxc.csv  -> Tiempos del algoritmo Filas x Columnas
#
# Formato de ejecucion:
#   mpirun -hostfile <file> -np <p> ./ejecutable <DimMatriz> <NumHilos>
# ==============================================================================================

use strict;
use warnings;

# ==============================================================================================
# PARAMETROS DE LA BATERIA DE EXPERIMENTOS
# ==============================================================================================

# Archivo hostfile con los nodos workers del cluster
my $hostfile = "filehost";

# Numero de repeticiones por configuracion (minimo 30 )
my $N_REPS = 30;

# Ejecutables a comparar
my @ejecutables = (
    { bin => "./mxmOmpMPIfxt", nombre => "FxT", salida => "resultados_fxt.csv" },
    { bin => "./mxmOmpMPIfxc", nombre => "FxC", salida => "resultados_fxc.csv" },
);

# Configuraciones de experimentos: (np, N, nH)
#  N debe ser divisible por (np - 1) workers
#   np=2 => 1 worker  => cualquier N
#   np=4 => 3 workers => N divisible por 3
my @experimentos = (
    { np => 2, N => 512,  nH => 1 },
    { np => 2, N => 512,  nH => 2 },
    { np => 2, N => 512,  nH => 4 },
    { np => 2, N => 1024, nH => 1 },
    { np => 2, N => 1024, nH => 2 },
    { np => 2, N => 1024, nH => 4 },
    { np => 2, N => 2048, nH => 1 },
    { np => 2, N => 2048, nH => 2 },
    { np => 2, N => 2048, nH => 4 },
    { np => 4, N => 768,  nH => 1 },
    { np => 4, N => 768,  nH => 2 },
    { np => 4, N => 768,  nH => 4 },
    { np => 4, N => 1536, nH => 1 },
    { np => 4, N => 1536, nH => 2 },
    { np => 4, N => 1536, nH => 4 },
);

# ==============================================================================================
# EJECUCION DE LA BATERIA
# ==============================================================================================

foreach my $exe (@ejecutables) {
    my $archivo_salida = $exe->{salida};
    my $algoritmo      = $exe->{nombre};
    my $bin            = $exe->{bin};

    # Abrir archivo CSV de salida para este algoritmo
    open(my $fh, '>', $archivo_salida)
        or die "No se pudo abrir $archivo_salida: $!";

    # Encabezado del CSV
    print $fh "algoritmo,np,workers,N,nH,rep,tiempo_us\n";

    print "\n=== Iniciando experimentos: Algoritmo $algoritmo ===\n";

    foreach my $exp (@experimentos) {
        my $np      = $exp->{np};
        my $N       = $exp->{N};
        my $nH      = $exp->{nH};
        my $workers = $np - 1;

        # Verificar divisibilidad N / workers antes de ejecutar
        if ($workers > 0 && $N % $workers != 0) {
            print "  [SKIP] N=$N no es divisible por workers=$workers (np=$np)\n";
            next;
        }

        print "  Ejecutando: np=$np | N=$N | nH=$nH | reps=$N_REPS\n";

        for my $rep (1 .. $N_REPS) {
            # Construir y ejecutar el comando mpirun
            my $cmd = "mpirun --oversubscribe -hostfile $hostfile -np $np $bin $N $nH 2>/dev/null";
            my $salida = `$cmd`;

            # Extraer el tiempo en microsegundos de la salida del programa
            my $tiempo = 0;
            if ($salida =~ /(\d+)\s*$/) {
                $tiempo = $1;
            }

            # Guardar resultado en el CSV
            print $fh "$algoritmo,$np,$workers,$N,$nH,$rep,$tiempo\n";
        }
    }

    close($fh);
    print "  => Resultados guardados en: $archivo_salida\n";
}

print "\n=== Bateria de experimentos completada ===\n";
print "Archivos generados:\n";
foreach my $exe (@ejecutables) {
    print "  - $exe->{salida}\n";
}
