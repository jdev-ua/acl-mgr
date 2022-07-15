<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <h2><@spring.message "title.index"/></h2>
    <img src="bender.jpg" alt="<@spring.message "title.index"/>"/>
</@c.container>
