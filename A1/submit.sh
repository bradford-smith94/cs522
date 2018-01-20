#!/bin/bash
# Bradford Smith (bsmith8)
# CS 522 Assignment 1 submit.sh
# 01/19/2018

AUTHOR='Bradford_Smith'
ASSIGNMENT='assignment1'
ARCHIVE="${AUTHOR}_$ASSIGNMENT.zip"
TMPDIR=$(mktemp -d)
PREFIX="$TMPDIR/$AUTHOR"

mkdir -p "$PREFIX"
echo "Copying everything to $PREFIX"
cp -r . "$PREFIX"

if [ -f "$PREFIX/.gitignore" ]; then
    echo "Removing excludes using .gitignore"
    while read -r arg; do
        if [[ "$arg" == /* ]]; then
            #shellcheck disable=SC2206
            dirs=($PREFIX/*$arg $PREFIX/.$arg)
            #fail if PREFIX is empty to avoid system destruction (SC2115)
            rm -r "${dirs[@]}" 2>/dev/null
        else
            #fail if PREFIX is empty to avoid system destruction (SC2115)
            rm -r "${PREFIX:?}/$arg" 2>/dev/null
        fi
    done < "$TMPDIR/$AUTHOR/.gitignore"
fi

echo "Removing assignment specific excludes"
rm "${PREFIX:?}/cs522_${ASSIGNMENT}_rubric.pdf"
rm "${PREFIX:?}/cs522_${ASSIGNMENT}_spec.pdf"

#need to enter temp dir for zip to execute
echo "Entering $TMPDIR"
#shellcheck disable=SC2164
pushd "$TMPDIR"

echo "Creating archive"
zip -r "$ARCHIVE" "$AUTHOR"

echo "Leaving $TMPDIR"
#shellcheck disable=SC2164
popd

mv "$TMPDIR/$ARCHIVE" .
rm -r "$TMPDIR"
