<#import "/spring.ftl" as spring/>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/home"><i class="fa-solid fa-shield-dog"
                                            style="color: darkgrey"></i><@spring.message "title.navbar"/></a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
            <li class="nav-item active">
                <a class="nav-link" href="/home"><@spring.message "link.home"/></a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/acls"><@spring.message "link.acls"/></a>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown" aria-expanded="false"><@spring.message "link.admin"/></a>
                <div class="dropdown-menu">
                    <a class="dropdown-item" href="/users"><@spring.message "link.users"/></a>
                    <a class="dropdown-item" href="#"><@spring.message "link.dictionaries"/></a>
                </div>
            </li>
        </ul>
    </div>

    <div class="form-inline">
        <button type="button" class="btn btn-secondary ml-2" onClick="location.href='/login'"><@spring.message "button.login"/></button>
        <button type="button" class="btn btn-outline-secondary ml-2" onClick="location.href='/logout'"><@spring.message "button.logout"/></button>
    </div>
</nav>