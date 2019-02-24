from django.conf.urls.static import static
from django.contrib import admin
from django.urls import include, path

from sec import settings

urlpatterns = [
    path('', include('home.urls')),
    path('admin/', admin.site.urls),
    path('user/', include('user.urls')),
    path('projects/', include('projects.urls')),
    path('payment/', include('payment.urls')),
]

if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)