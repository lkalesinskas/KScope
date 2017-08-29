<h1>KScope</h1>

<h2>Usage</h2>

The command line call should look something like the following:

```bash
java -jar KScopeJAR.jar -ram t -pca spanPCA -testin testOut6.fasta -trainin trainOut6.fasta -out outfile.fasta -numthread 10
```

<h3>-ram</h3>

t/f where t will use the RAM version for storage and f will use the database for storing the training file


<h3>-pca</h3>

the location and filename of the PCA file. In the example above spanPCA is the filename and it is being called locally and has no file extension
-testin

the file that will be used to test against the training data
-trainin

the file that will be used to train the model
-out

the file name for the output file
-traindb

used when using the Database version only. Will train the database using the train file supplied by the user
-numthread

specify the number of threads the program will use to test data. In the example above, 10 threads are being used
-fastatofeature

turns a FASTA file into a FEATURE file
-kmer

the kmer count you wish to use
-help
display this message