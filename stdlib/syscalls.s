.globl read
.type read, @function
.globl write
.type write, @function
.globl fsync
.type fsync, @function
.globl exit
.type exit, @function

.text
write:
    pushl %ebp
    movl %esp, %ebp
    pushl %ebx
    
    movl 16(%ebp), %edx
    movl 12(%ebp), %ecx
    movl 8(%ebp), %ebx
    movl $4, %eax
    int $0x80

    popl %ebx
    movl %ebp, %esp
    popl %ebp
    ret

read:
    pushl %ebp
    movl %esp, %ebp
    pushl %ebx

    movl 16(%ebp), %edx
    movl 12(%ebp), %ecx
    movl 8(%ebp), %ebx
    movl $3, %eax
    int $0x80

    popl %ebx
    movl %ebp, %esp
    popl %ebp
    ret

fsync:
    pushl %ebp
    movl %esp, %ebp
    pushl %ebx
    
    movl 4(%ebp), %ebx
    movl $118, %eax
    int $0x80
    movl $0, %eax

    popl %ebx
    movl %ebp, %esp
    popl %ebp
    ret

exit:
    movl 4(%esp), %ebx
    movl $1, %eax
    int $0x80
