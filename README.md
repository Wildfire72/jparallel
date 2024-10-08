# Welcome to Jparallel
 This research project aims to make multi-threaded programming in Java easier to accomplish. The ANTLR (Another Tool for Language Recognition) parser generator will be utilized to achieve this. 
 With ANTLR, we can define atomic portions of code known as "tokens" and the rules in which they can be typed out with context to one another known as the grammar. 
 With this, we can add a new token keyword "parallel" along with the already defined curly brace tokens "{" and "}" to create a new block type in Java.
 Every root statement within this block will then be run in parallel, requiring the tool to create a runnable class and override the run method within the class for each statement.
