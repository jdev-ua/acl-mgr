<#import "/spring.ftl" as spring/>
<#import "container.ftlh" as c>
<@c.container>
    <h2><@spring.message "index.title"/></h2>
    <img src="bender.jpg" alt="<@spring.message "index.title"/>"/>
</@c.container>
