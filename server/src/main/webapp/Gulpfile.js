var gulp       = require('gulp'),
    browserify = require('gulp-browserify'),
    concat     = require('gulp-concat'),
    sass       = require('gulp-sass'),
    watch      = require('gulp-watch'),
    connect    = require('gulp-connect'),
    bower      = require('gulp-bower'),
    rimraf     = require('rimraf');

    var merge = require('merge-stream');

var DEST = __dirname + '/build'

var config = {
     bowerDir: __dirname + '/bower_components' 
}

gulp.task('rimraf', function (cb) {
  rimraf(DEST + '/*', cb);
});

gulp.task('bower', function() { 
    return bower()
         .pipe(gulp.dest(config.bowerDir)) 
});

gulp.task('styles', function () {

    var sassStream, cssStream;
    //http://ypereirareis.github.io/blog/2015/10/22/gulp-merge-less-sass-css/
    //compile sass
    sassStream =
      gulp.src('client/scss/main.scss')
       .pipe(sass({
                includePaths: [
                    config.bowerDir + '/bootstrap-sass/assets/stylesheets'
                ]
            }))

    //select additional css files
    cssStream = gulp.src(config.bowerDir + '/c3/c3.css');

    //merge the two streams and concatenate their contents into a single file
    return merge(sassStream, cssStream)
        .pipe(concat('main.css'))
        .pipe(gulp.dest('build/css/'));
});

gulp.task('scripts', function () {
  gulp.src(['client/js/app.js'])
      .pipe(browserify({
          debug: true,
          transform: [ 'reactify' ]
      }))
      .pipe(gulp.dest('build/js/'));
});

gulp.task('copy', function(){
  gulp.src('client/*.html')
    .pipe(gulp.dest('build/'));
});

//http://stackoverflow.com/questions/16748737/grunt-watch-error-waiting-fatal-error-watch-enospc
//or echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf && sudo sysctl -p
gulp.task('watch', function() {
  gulp.watch('client/js/**/*.js', [ 'scripts' ]);
  gulp.watch('client/scss/**/*.scss', [ 'styles' ]);
  gulp.watch('client/*.html', [ 'copy' ]);
});

gulp.task('webserver', function() {
  connect.server({
    livereload: false,
    port: 8000,
    root: ['build']
  });
});

gulp.task('build', [ 'bower', 'rimraf', 'styles', 'scripts', 'copy' ]);
gulp.task('default', ['build', 'webserver', 'watch']);

