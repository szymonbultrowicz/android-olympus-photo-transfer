import http.server
import cgitb
import re

cgitb.enable()

server = http.server.HTTPServer


# Treat everything as a cgi file, i.e.
# `handler.cgi_directories = ["*"]` but that is not defined, so we need
class Handler(http.server.CGIHTTPRequestHandler):
    def __init__(self, *args, directory=None, **kwargs):
        super().__init__(*args, directory=None, **kwargs)
        self.cgi_info = '', self.path[1:]

    def is_cgi(self):
        collapsed_path = http.server._url_collapse_path(self.path)
        dir_sep = collapsed_path.find('/', 1)
        head, tail = collapsed_path[:dir_sep], collapsed_path[dir_sep + 1:]
        file_path = re.compile('\\?.*').sub('', tail)
        if file_path.endswith(".cgi"):
            self.cgi_info = head, tail.replace('.cgi', '.py')
            self.path = tail.replace('.cgi', '.py')
            return True
        return False


httpd = http.server.HTTPServer(("", 8000), Handler)
httpd.serve_forever()
