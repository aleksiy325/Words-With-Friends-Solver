# Words-With-Friends-Solver


Solver for Words With Friends mobile game. 
Wordlist is Enhanced North American Benchmark Lexicon downloaded from here: http://www.greenworm.net/notes/2011/05/02/words-friends-wordlist

Usage:
Create a txt file with the state of the board encoded as follows. An example is also given in board.txt
```
2  // number of words
7 7 A word //x y dir word 
7 7 D word
7 // number of letters  in hand
a b c d e f g // letters in hand
```

Run with the name of the txt file as an argument.
Output will be the a list of words that can be played ordered by score along with their location.
