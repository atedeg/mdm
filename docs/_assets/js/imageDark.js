document.querySelector('html')
    .classList.contains("theme-dark")
    ? $('#clean-arch').append('<img alt="Clean Architecture" src="../images/cleanArchitecture_dark.svg"/>')
    : $('#clean-arch').append('<img alt="Clean Architecture" src="../images/cleanArchitecture.svg"/>')
