#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

const int PORT = 12345;

void error(char *msg) __attribute__((noreturn));
void error(char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char **argv)
{
    if (argc != 3) {
       fprintf(stderr, "Usage: %s <board-state-json> <time-remaining>\n", argv[0]);
       exit(1);
    }
    const char* board_state_json = argv[1];
    //const char* time_remaining = argv[1]; // currently unused

    // Create socket.
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) 
        error("Error creating socket");

    // Setup localhost address.
    struct sockaddr_in target;
    memset(&target, 0, sizeof(target));
    target.sin_family = AF_INET;
    target.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    target.sin_port = htons(PORT);

    // Connect to server.
    if (connect(sock, (struct sockaddr*) &target, sizeof(target)) < 0) 
        error("Error connecting");

    int n;

    // Send request.
    n = write(sock, board_state_json, strlen(board_state_json));
    if (n < 0) 
        error("Error writing to socket");
    shutdown(sock, SHUT_WR);

    // Read and print response.
    char buf[1024];
    while ((n = read(sock, buf, sizeof(buf))) > 0) {
        fwrite(buf, sizeof(char), n, stdout);
    }
    if (n < 0)
        error("Error reading from socket");

    close(sock);
    return 0;
}
