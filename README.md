# Text-Editor

Watch a demo here: https://youtu.be/gjSG-mXhFdA

I created a text editor from scratch in Java.  Has the following features:
- arrow keys
- font size changes
- insert character
- delete character
- open/save
- undo/redo
- mouseclick
- wordwrap
- textwrap

Not fully implemented:
- scrollbar
- highlight

Design decisions:  I wanted to design the editor so that it had fast insertion and deletion times and would be able to word wrap easily.  To do so, I used a double ended linked list that would store each line and an array list of pointers to the beginning of each line.  The benefit of a linked list is fast insertion and deletion.  The array list of pointers, instead of separate linked lists means that we do not have to make another linked list or copy over most of one when a word wraps.  This design choice however runs into problems when trying to implement search because it would not work quickly enough.  I had to choose what functions I wanted to prioritize runtime for, for this project.

Run by using command java editor.Editor <filename.txt>

![Text Editor Screenshot](/demo/text-editor-demo.png?raw=true "Demo")

