<#import "/spring.ftl" as spring/>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <a class="navbar-brand" href="/home"><@spring.message "navbar.title"/></a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNav">
    <ul class="navbar-nav">
      <li class="nav-item active">
        <a class="nav-link" href="/home"><@spring.message "navbar.button.home"/></a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="/acls"><@spring.message "navbar.button.acls"/></a>
      </li>
    </ul>
  </div>
</nav>