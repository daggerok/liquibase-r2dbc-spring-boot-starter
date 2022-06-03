const base = process.env.BASE || '/';

module.exports = {
  base,
  head: [
    ['link', {rel: 'icon', href: base + 'favicon.ico'}],
  ],
  docsDir: 'docs',
  themeConfig: {
    repo: 'daggerok/liquibase-r2dbc-spring-boot-starter-parent',
  }
};
