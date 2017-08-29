<h1>KScope</h1>
<h2>Breakdown</h2>

* Run Sequence Analysis on your genes.

* Quick classification of a large metagenomic set

* Faster than BLASTing due to useage and storage inside of RAM

* Uses Principle Component Analysis (PCA) to analyze the importance of kmers

<h2>Usage</h2>

The command line call should look something like the following:

```bash
java -jar KScopeJAR.jar -fastatofeature f -pca spanPCA -testin testOut6.fasta -trainin trainOut6.fasta -out outfile3mer.fasta -numthread 10 -kmer 3
```

<h3>-fastatofeature</h3>

t/f where t will turn the supplied file with the -trainin command and turn it into a feature file


<h3>-pca</h3>

the location and filename of the PCA file. In the example above spanPCA is the filename and it is being called locally and has no file extension


<h3>-testin</h3>

the file that will be used to test against the training data


<h3>-trainin</h3>

the file that will be used to train the model


<h3>-out</h3>

the file name for the output file

<h3>-numthread</h3>

specify the number of threads the program will use to test data. In the example above, 10 threads are being used

<h3>-kmer</h3>

the kmer count you wish to use


<h3>-help</h3>
display this message

<h1>Files</h1>
<a href="https://drive.google.com/open?id=0B81VTJn7f64-Ukg5bWVxeFlfa28">link to Google drive</a>

<h1>Questions? Problems?</h1>
email: maxrkelly@gmail.com

[link]:https://drive.google.com/open?id=0B81VTJn7f64-Ukg5bWVxeFlfa28
