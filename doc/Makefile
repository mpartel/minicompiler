all: ast-example.png

ast-example.png: ast-example.dot
	dot -Tpng -o$@ < $<

# No 'clean' target because the image is meant to be in version control since it's used in the readme.
