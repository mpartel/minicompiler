
#include <stddef.h>

// Syscalls
extern void write(unsigned int fd, const char *buf, size_t count);
extern void fsync(unsigned int fd);

void printInt(int x)
{
    char buf[32];
    int i = 31;
    int minus = (x < 0);
    
    buf[i--] = '\n';
    
    do {
        int digit = x % 10;
        buf[i--] = 0x30 + digit;
        x = x / 10;
    } while (x > 0);
    
    if (minus) {
        buf[i--] = '-';
    }
    
    i++;
    
    const char *start = &buf[i];
    int len = 32 - i;
    write(1, start, len);
    fsync(1);
}
