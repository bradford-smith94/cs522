#!/bin/bash
# Bradford Smith (bsmith8)
# CS 522 Assignment 1 submit.sh
# 01/19/2018

AUTHOR='Bradford_Smith'
ASSIGNMENT='assignment1'
ARCHIVE="$AUTHOR-$ASSIGNMENT.zip"
TMPDIR=$(mktemp -d)

mkdir -p "$TMPDIR/$AUTHOR"
echo "Copying everything to $TMPDIR/$AUTHOR"
cp -r ./* "$TMPDIR/$AUTHOR"

echo "Entering $TMPDIR"
#shellcheck disable=SC2164
pushd "$TMPDIR"

#TODO: remove excludes
echo "Creating archive"
zip -r "$ARCHIVE" "$AUTHOR"

echo "Leaving $TMPDIR"
#shellcheck disable=SC2164
popd

mv "$TMPDIR/$ARCHIVE" .
rm -r "$TMPDIR"
