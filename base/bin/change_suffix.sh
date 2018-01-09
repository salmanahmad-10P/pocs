for file in *.PNG; do
    mv "$file" "`basename $file .PNG`.png"
done
