# cljs-browser-repl

A clojurescript repl in your browser! 😱

Visit the deployed page at
[chimeces.com/cljs-browser-repl](http://chimeces.com/cljs-browser-repl/).

## Current status

* Repl implemented
  * Most of the repl works, with the exception of `require` and friends, still have to figure what to do with these :)
* Repl UI
  * History
  * Auto scroll bottom
  * Auto-size text input
  * Don't submit unreadable forms (insert new-lines)
  * Shift+enter makes new line
  * TBD:
    * Navigate history
      * From text input going up/down.
      * Some form of tapping a form/result should populate the input with it.
    * Max-height for the text-input.
* Other crazy things
  * Rendering of other types of forms in the repl history (read meta or `:type`, render as appropiate).
    * Markdown
    * Image url
    * HTML
  * Load history contents from url or uploaded file.
  * Save history contents to gist/url or download file.
  * Edit the history in the repl.
  * Loading libraries from cljsjs or somewhere else.
  * Provide default utilities in the repl (for creating a floating movable iframe to eval in? something like that?).
  * Add lessons support (load lesson exercises, check responses for match, move to next lesson).

## Contributing

Please. Needs java, leiningen, node & npm.

### In dev

`rlwrap lein figwheel` Will start the dev server and cljs compilation.

In another terminal do `npm start` for kicking off the style builder.

Do not commit the `resources/public/style.css` or
`resources/public/js/compiled/cljs_browser_repl.js`, I'll update those when
making a deployment.

### For deploying

In general I'll deploy so don't commit the previously mentioned assets.
Instructions are:

Generating js: `lein clean && lein cljsbuild once min`
Generating css: `npm run build`
For deploying use git subtree to push the `resources/public` subtree to the
branch `gh-pages`.

## License

Copyright © 2015 Joaquin Oltra

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
