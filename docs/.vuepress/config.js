const localNpmVersionPlugin = require('./plugins/local-npm-version.plugin');
const { description, version } = require('../package.json');
const title = `${description} v${version}`
const base = process.env.BASE || '/';

module.exports = {
  base,
  title,
  head: [
    ['link', {rel: 'icon', href: base + 'favicon.ico'}],
  ],
  locales: {
    '/': {
      lang: 'en-US',
    },
  },
  themeConfig: {
    docsRepo: 'https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter',
    docsBranch: 'master',
    docsDir: 'docs',
    editLink: true,
    // editLinkPattern: ':repo/-/edit/:branch/:path',
    locales: {
      '/': {
        selectLanguageName: 'English',
      },
    },
  },
  plugins: [
    localNpmVersionPlugin(),
  ],
};
