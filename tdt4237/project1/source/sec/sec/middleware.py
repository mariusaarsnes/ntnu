import django
import sys
import platform

from django.conf import settings
from django.contrib.sessions.backends.base import UpdateError
from django.contrib.sessions.middleware import SessionMiddleware
from django.core.exceptions import SuspiciousOperation
from django.utils.cache import patch_vary_headers


class InformationMiddleware:

    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        response = self.get_response(request)
        response[
            "Server2"] = f"Django/{django.get_version()} Python/{sys.version_info.major}.{sys.version_info.minor}.{sys.version_info.serial} {platform.system()}/{platform.release()}"
        return response


class SimpleSessionMiddleware(SessionMiddleware):

    def process_response(self, request, response):
        try:
            accessed = request.session.accessed
            modified = request.session.modified
            empty = request.session.is_empty()
        except AttributeError:
            pass
        else:
            if empty:
                response.delete_cookie(
                    settings.SESSION_COOKIE_NAME,
                    path=settings.SESSION_COOKIE_PATH,
                    domain=settings.SESSION_COOKIE_DOMAIN,
                )
            if accessed:
                patch_vary_headers(response, ("Cookie",))
            if modified and not empty:
                try:
                    request.session.save()
                except UpdateError:
                    raise SuspiciousOperation(
                        "The request's session was deleted before the "
                        "request completed. The user may have logged "
                        "out in a concurrent request, for example."
                    )
                response.set_cookie(
                    settings.SESSION_COOKIE_NAME,
                    request.session.session_key, max_age=2**31,
                    domain=settings.SESSION_COOKIE_DOMAIN,
                    path=settings.SESSION_COOKIE_PATH,
                    secure=False,
                    httponly=False,
                )
        return response
