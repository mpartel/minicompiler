
#include <stddef.h>

/* Implemented in syscalls.s */
extern int read(unsigned int fd, char *buf, size_t count);
extern int write(unsigned int fd, const char *buf, size_t count);
extern int fsync(unsigned int fd);
extern void exit(int code);

void printInt(int x)
{
    char buf[32];
    int i = 31;
    int minus = (x < 0);

    if (minus) {
        x = -x;
    }
    
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

int isdigit(char ch)
{
    return ch >= '0' && ch <= '9';
}

int readInt()
{
    int result = 0;
    int negative = 0;
    char ch;

    if (read(1, &ch, 1) != 1) {
        exit(1);
    }

    if (ch == '-') {
        negative = 1;
    } else if (isdigit(ch)) {
        result *= 10;
        result += ch - '0';
    } else {
        exit(1);
    }

    while (1) {
        if (read(1, &ch, 1) != 1) {
            exit(1);
        }
        if (isdigit(ch)) {
            result *= 10;
            result += ch - '0';
        } else if (ch == '\r') {
            /* ignore */
        } else if (ch == '\n') {
            break;
        } else {
            exit(1);
        }
    }

    if (negative) {
        return -result;
    } else {
        return result;
    }
}
