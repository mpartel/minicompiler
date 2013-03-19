#!/bin/sh -e
cd `dirname "$0"`

if [ -z "$1" ]; then
    for file in *.minilang; do
        ./compile-and-run.sh $file
    done
    exit 0
fi

SRC="$1"

if [ ! -e "$SRC" ]; then
    echo "No such file: $SRC"
    exit 1
fi

BASENAME=`basename "$SRC" .minilang`

echo "Compiling"
java -jar ../target/minicompiler-dev.jar "$SRC" > "$BASENAME.s"
echo "Assembling"
as --32 -march=i686 -o "$BASENAME.o" "$BASENAME.s"
echo "Linking"
ld -melf_i386 -o "executable-$BASENAME" "$BASENAME.o" ../stdlib/stdlib.o ../stdlib/syscalls.o

echo "Running"
./executable-"$BASENAME"
