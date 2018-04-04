# Google Drive FileUtils
GDFU is a set of Java Utils classes designed for the Google Drive API. It occupies a "middle ground"; easier to use and significantly more convenient than the low-level API libraries that Google provides. GDFU is designed to make novices immediately productive yet also expose the full power of the Google Drive API.

## How to use?
* Init GDFU utils class
```
GDFileUtils gdfu = new GDFileUtils(credential);
```
* Upload file to Google Drive
```
gdfu.upload(metadata, localFile, Arrays.asList("0B64VHJrvrPWHc09wdHh0aXFOYjg"));
```
* List files of Google Drive folder
```
Collection<java.io.File> allFiles = gdfu.listFiles("0B64VHJrvrPWHc09wdHh0aXFOYjg", "trashed != true", Arrays.asList("id", "name"));
```
* Download file from Google Drive
```
gdfu.download("1tGxfv46nbimltwlgdcnmk_NOg_E9lcM0", new java.io.File("/Downloads/sample.png"));
```
and more...
