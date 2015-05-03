SASL Compiler
============

A compiler for SASL, a functional programming language developed in 1972 (http://en.wikipedia.org/wiki/SASL_(programming_language). Result of a student project at the University of Tübingen. Development took part together with Benjamin Böhm.

Usage
=====

Tests can be run with
    
    ant tests

Build can be run with

    ant build

A jar can be cuild with

    ant jar

Either start the sasl\_compiler.jar, type your programm and end it with CTRL+D _or_ pipe in the code like this:

    java -jar sasl_compiler.jar < myprogramm.sasl

An example program outputting the first 5000 primes is included in this repo. Start it with

    java -jar sasl_compiler.jar < primesieve.sasl

Examples
========

A more thorough introduction to the language can be found on the manual page of the JavaScript port of this compiler: http://patrickbrosi.de/jsasl/manual.html

Simple addition
---------------
####Input
    5+4

####Output
    9

Variable scopes
---------------
####Input
    ((a where a x=(b where b=x+(c where c=x))) 11)+1

####Output
    23

Prime sieve
----------
####Input
```
def take n l = if n=0 or l=nil then nil
	else x:take (n-1) xs where x = hd l;
				   xs = tl l
def mod x y = (x - (x/y)*y)
def primes = sieve (naturals 2)

def sieve input = (hd input) : (sieve (removeFromList (tl input) (hd input)))

def removeFromList list ele =if (mod (hd list) ele) = 0
							then (removeFromList (tl list) ele)
						else (hd list) : (removeFromList (tl list) ele)

def	naturals x = x : (naturals (x+1))

.

take 50 primes
```
####Output
    [2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229]
