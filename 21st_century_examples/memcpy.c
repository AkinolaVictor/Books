/* Compile with:
export CFLAGS="-g -Wall -std=gnu11 -O3"  #the usual.
make memcpy
*/
#include <assert.h>
#include <string.h> //memcpy

int main(){
    int abc[] = {0, 1, 2};
    int *copy1, copy2[3];

    copy1 = abc;
    memcpy(copy2, abc, sizeof(int)*3);

    abc[0] = 3;
    assert(copy1[0]==3);
    assert(copy2[0]==0);
}
