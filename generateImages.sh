#! /bin/bash
#
# Thomas Freese
# Script zum Erzeugen der Einzelbilder der Simulationen.
#
# ffmpeg -f image2 -r 25  -i gof-%05d.png   -c:v png -r 25 -an               gof.avi
# ffmpeg -f image2 -r 25  -i wator-%05d.png -c:v png -r 25 -an -f matroska wator.mkv
# ffmpeg -f image2 -r 250 -i ants-%05d.png  -c:v png -r 25 -an -f matroska ants.mkv

# 3840 2160
# 1920 1080
#java -cp target/cellular-machines-assembly-jar-with-dependencies.jar de.freese.simulationen.SimulationConsole -type ants -cycles 15000 -size 3840 2160 -dir /tmp/simulationen

#java -jar target/cellular-machines-shaded-jar-with-dependencies.jar
java -cp target/cellular-machines-shaded-jar-with-dependencies.jar de.freese.simulationen.SimulationConsole -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen
