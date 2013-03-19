
all: stdlib.o syscalls.o

stdlib.o: stdlib.c
	gcc -g0 -Os -nostdlib -ffreestanding -c $<

syscalls.o: syscalls.s
	as -o$@ $<

clean:
	rm -f stdlib.o syscalls.o
