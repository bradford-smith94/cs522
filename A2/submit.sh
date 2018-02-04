#!/bin/bash
# Bradford Smith (bsmith8)
# CS 522 Assignment 2 submit.sh
# 02/04/2018

AUTHOR='Bradford_Smith'
ASSIGNMENT='assignment2'
ARCHIVE="${AUTHOR}_$ASSIGNMENT.zip"
TMPDIR=$(mktemp -d)
PREFIX="$TMPDIR/$AUTHOR"

mkdir -p "$PREFIX"
echo "Copying everything to $PREFIX"
cp -r . "$PREFIX"

GITIGNORES=$(find "$PREFIX" -type f -name .gitignore)

echo "Removing excludes using .gitignore(s)"
while read -r f; do
    front=$(dirname "$f")
    while read -r arg; do
        if [[ "$arg" == /* ]]; then
            #shellcheck disable=SC2206
            dirs=($front/*$arg $front/.$arg)
            rm -r "${dirs[@]}" 2>/dev/null
        else
            #fail if 'front' is empty to avoid system destruction (SC2115)
            rm -r "${front:?}/$arg" 2>/dev/null
        fi
    done < "$f"
done <<< "$GITIGNORES"

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
