.globl write
.type write, @function
.globl fsync
.type fsync, @function

.text
write:
    pushl %ebp
    movl %esp, %ebp
    
    movl 16(%ebp), %edx
    movl 12(%ebp), %ecx
    movl 8(%ebp), %ebx
    movl $4, %eax
    int $0x80
    movl $0, %eax
    
    movl %ebp, %esp
    popl %ebp
    ret

fsync:
    pushl %ebp
    movl %esp, %ebp
    
    movl 4(%ebp), %ebx
    movl $118, %eax
    int $0x80
    movl $0, %eax
    
    movl %ebp, %esp
    popl %ebp
    ret
