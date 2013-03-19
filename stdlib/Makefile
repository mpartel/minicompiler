
all: stdlib.o syscalls.o

stdlib.o: stdlib.c
	gcc -m32 -g0 -Os -nostdlib -ffreestanding -c $<

syscalls.o: syscalls.s
	as --32 -march=i686 -o$@ $<

clean:
	rm -f stdlib.o syscalls.o
