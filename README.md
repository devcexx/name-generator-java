# name-generator-java
This is a simple utility that allows to generate any kind of random names based on another set of names.

This utility works by generating a relation between each character of the input dataset words, and the probabilities of other characters of appearing next to it. This process may be modelized as a directed weighted graph, or a Márkov-like system, where the vertices represent the available characters in the dataset. The weight of edges of the graph represent the possibility of a character to be followed by another.

Since calculating the probabilities character by character may result in too granulated results while generating names, them may be calculated using groups of characters of a specific size instead. If the group size is bigger, the results may have more sense. If it is too high, many of the results may be pretty similar to the names contained in the original dataset, or even equal. The group size should be adjusted for the different input datasets.

## Compiling the program
The program can be compiled using Maven:
```
mvn clean package
```

After that, a jar file is generated into the `target/` folder.

## Building the training data
Before start generating names, a "training data" must be built from a name set. This training data contains the probabilities explained before, so the program doesn't need to compute them each time.

A training data may be generating like this:
```
$ java -cp NameGenerator.jar com.devcexx.namegen.TrainSetGenerator 1 < my_dataset.txt > train_data.gz
```

Where `NameGenerator.jar` is the path of the compiled artifact, `my_dataset.txt` is a file containing the names that will be used as input data, one per line; and `train_data.gz` is the output training data.
The `1` determines the group size of the train data. It can be increased up to 10 as desired.

## Generating names
After generating the training data, its time to generate some names:

```
$ java -cp NameGenerator.jar com.devcexx.namegen.NameGenerator < train_data.gz | head -10
```

When the class is executed, it will print to the standard output random numbers until it is stopped. So, if we want to generate a speicific number of names, we can pipe the output to a `head` command.

## Examples

Some example train data is present in the `example-trainsets/` folder and can be used to test out the generator. The names of the examples has the form of `description_g<x>.gz` where `x` is the group size used in that training data.

## License
This program is licensed under the GNU General Public License v3.0. For more information, see the file LICENSE of the repository.



