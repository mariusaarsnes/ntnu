from django.contrib.auth.hashers import MD5PasswordHasher


class CustomMD5PasswordHasher(MD5PasswordHasher):

    def salt(self):
        return "tdt4237"
