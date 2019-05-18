# grhw

A sample date service implementation.

## Usage

To run the data service, run the following leiningen command:

    lein run <file-1> ... <file-n>

Each file provided on the command is parsed and incorporated into the initial
data service state.  Prior to this, the service will print to stdout the
result of various sorting algorithms on the parsed data.

To generate a sample data file run the following:

    lein genfiles >data.txt

This project uses the cloverage plugin to generate test coverage data.  To
view these results, run `lein cloverage`.

