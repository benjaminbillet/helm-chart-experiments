# Very dirty script, for proof of concept only

import requests
import sys
import json

def main(argv):
  input = sys.stdin.read()
  inputjson = json.loads(input)
  #print(inputjson)

  dotStr = ""
  for node in inputjson:
    name = node["labels"]["instance"]
    cur_output = 1
    while True:
      try:
        outputTopic = node["labels"]["output" + str(cur_output)]
        for node2 in inputjson:
          cur_input = 1
          name2 = node2["labels"]["instance"]
          while True:
            try:
              inputTopic = node2["labels"]["input" + str(cur_input)]
              if inputTopic == outputTopic:
                dotStr = dotStr + "\"" + name + "\"->\"" + name2 + "\" [label=" + inputTopic + "];"
              cur_input = cur_input + 1
            except KeyError:
              break
        cur_output = cur_output + 1
      except KeyError:
        break

  dotStr = "graph {" + dotStr + "}"
  print(dotStr)
  print(dot_to_ascii(dotStr))

def dot_to_ascii(dot: str, fancy: bool = True):

  url = 'https://dot-to-ascii.ggerganov.com/dot-to-ascii.php'
  boxart = 0

  # use nice box drawing char instead of + , | , -
  if fancy:
    boxart = 1

  params = {
    'boxart': boxart,
    'src': dot,
  }

  response = requests.get(url, params=params).text

  if response == '':
    raise SyntaxError('DOT string is not formatted correctly')

  return response


if __name__ == "__main__":
  main(sys.argv[1:])
