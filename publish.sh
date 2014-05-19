#!/bin/sh

echo " ======================== "
echo " Updating gh-pages branch"
echo " ======================== "

if git diff-index --quiet HEAD --; then

    mv tmp .tmp

    git co gh-pages

    rm -rf *
    mv .tmp/* .
    rm -rf .tmp
    rm publish.sh

    echo " ======================== "
    echo "            WIN          "
    echo " ======================== "
    echo " Please check the updated site and checkin ..."
else
    echo " ======================== "
    echo "            FAIL          "
    echo " ======================== "
    echo "You have changes not checked-in - cannot automatically update gh-pages"
fi
