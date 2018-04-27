#!/bin/sh


# alarm clock launcher
# author: lcn
# Usage: 
# normal mode (wall clock display without alarm mode)
#    ./run.sh
# alarm mode
#    ./run.sh <hhmm>
# where hh is a two-digit hour ranging from [00, 23] and mm is a two-digit minute ranging from [00, 59]

echo_err() {
    echo "$@" 1>&2
}

if [ $# -gt 1 ]; then
    echo_err "Usage:"
    echo_err "Normal Mode: "
    echo_err "    ./run.sh"
    echo_err "Alarm Mode: "
    echo_err "    ./run.sh <hhmm>"
    echo_err "where hh is a two-digit hour ranging from [00, 23] and mm is a two-digit minute ranging from [00, 59]"
    exit 1
fi

make
case $# in
    0)
        make run
        ;;
    1)
        make run ARGS="--alarm=$1"
        ;;
esac
make clean
