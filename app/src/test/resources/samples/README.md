# Sample camera servers

Sample server that imitates your camera are useful to test the app against locally run server, instead of connecting to the physical camera each time.

## Preparation

Unfortunatley, due to the sizing issues (a single file has ~15-20MB, I cannot embed the sample data to the repository.

In order to prepare sample photos, follow the instructions below.
1. Install UFRaw (see: [UFRaw download page](http://ufraw.sourceforge.net/Install.html))
1. Go to the sample directory (e.g. `cd many-files`)
1. Run the `fetch-test-data.sh` script

## Running

Run any http server you want inside the `<sample-name>` directory, i.e.:

```
cd many-files
python3 -m http.server
```

Run the app and change the camera server properties in the settings.