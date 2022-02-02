# Read from the file file.txt and output the tenth line to stdout.

# 1. sed
sed -n '10p' ../files/file.txt

# 2.tail
tail -n +10 ../files/file.txt | head -n 1

# 3.awk
awk 'NR==10' ../files/file.txt