# 1.
cat ../files/words.txt | tr -s ' ' '\n' | sort | uniq -c | sort -r | awk '{ print $2, $1 }'

# 2.
cat words.txt | xargs -n 1 | sort | uniq -c | sort -nr | awk '{print $2" "$1}'


# 3.
awk '{for(i=1;i<=NF;i++){asso_array[$i]++;}};END{for(w in asso_array){print w,asso_array[w];}}' words.txt | sort -rn -k2

#