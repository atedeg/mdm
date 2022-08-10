document.querySelectorAll("pre.mermaid, pre>code.language-mermaid").forEach($el => {
    console.log($el)
    // if the second selector got a hit, reference the parent <pre>
    if ($el.tagName === "CODE")
        $el = $el.parentElement.parentElement
    // put the Mermaid contents in the expected <div class="mermaid">
    // plus keep the original contents in a nice <details>
    $el.outerHTML = `
    <div class="mermaid text-center">${$el.textContent}</div>  `
})