const { version } = require('../../package.json');

const localNpmVersionPlugin = (options) => {
  return (app) => {
    return {
      name: 'vuepress-plugin-local-npm-version',
      extendsPageOptions: (pageOptions, app) => {
        if (!!app && !!app.siteData && !app.siteData.version) {
          app.siteData.version = version;
        }
      },
    };
  };
};

module.exports = localNpmVersionPlugin;
