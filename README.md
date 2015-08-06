# cljs-browser-repl

A clojurescript repl in your browser! 😱

Visit the deployed page at
[chimeces.com/cljs-browser-repl](http://chimeces.com/cljs-browser-repl/).

## Current status

Still figuring things out, can't define vars or functions yet, doc is not
working.

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
