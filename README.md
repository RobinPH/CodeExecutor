# CodeExecutor
CodeExecutor enables player to write and execute their code inside your server. CodeExecutor uses [Piston](https://github.com/engineer-man/piston) code execution engine for the execution of every code.

Plugin: https://www.spigotmc.org/resources/codeexecutor.88710/

## Commands
```
/code - Main entry point of the commands
  /code new - Create a new editor
  /code open - Open the editor
  /code close - Close the editor
  /code run - Run the code in your editor
  /code language - Change the execution language
  /code languages - List of suported languages
  /code scroll - Scroll up/down in the editor
  /code goto - Go to a certain line in the editor
  /code line - Commands for line modifications
    /code line insert - Insert new line
    /code line delete - Delete line
    /code line edit - Edit a line's text
    /code line move - Move line to another line
  
  /code argv
    /code argv count - Set the number of arguments
    /code argv set - Set the arguments values
  
  /code stdin - Enable/disable stdin
```

## Permission
```
codeexecutor.code - Access all of CodeExecutor's functionality
```

## Config
```yml
editor-height: 20 # Recommended: 20 and 10
max-line-length: 192 # Recommended: 192. n / 6 == How many 'A' characters can you fit in the line
max-line-count: 1028 # Recommended: <= 1028
max-character-per-line: 128 # Recommended: >=64 && <= 128
max-output-line: 64 # Recommended: <= 64
max-size-per-output: 16384 # Max size per output, 16 kilobytes
```

## Usage
Basic Usage and Running
>![](https://media.giphy.com/media/IdhinkCKFrYnaW7ush/giphy.gif)

Use of \\n (New Line) and \\t (Tab)
>![](https://media.giphy.com/media/6vUG6OGITvSoEqcwYw/giphy.gif)

Basic Usage and Running
>![](https://media.giphy.com/media/IdhinkCKFrYnaW7ush/giphy.gif)

Use of Stdin
>![](https://media.giphy.com/media/siynILe35LJWsp0YVz/giphy.gif)

Use of argv
>![](https://media.giphy.com/media/QsbdHJVkBAqkJr0uHv/giphy.gif)

Line move, insert, and delete. Scroll, and go to
>![](https://media.giphy.com/media/vAwp6iBlGkJpI5GjnA/giphy.gif)
