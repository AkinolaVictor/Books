/* Compile with:
make size_t CFLAGS="-g -Wall"
*/
#include <stdio.h>

int main(){
    char *astring = "I am somwhere in memory.";
    size_t location = (size_t)astring;
    printf("%s\n", (char*)location);
}
