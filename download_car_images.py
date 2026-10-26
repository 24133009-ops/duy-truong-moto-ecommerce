import urllib.request
import urllib.parse
import json
import os
import shutil
import time

SEARCHES = {
    "toyota_lc300.jpg": "Toyota Land Cruiser 300 ZX",
    "jeep_wrangler.jpg": "Jeep Wrangler Rubicon front",
    "mazda_mx5.jpg": "Mazda MX-5 RF front",
    "honda_civic_typer.jpg": "Honda Civic Type R FL5",
    "nissan_gtr.jpg": "Nissan GT-R R35 front",
    "hyundai_palisade.jpg": "Hyundai Palisade front",
    "kia_carnival.jpg": "Kia Carnival KA4 front",
    "volkswagen_teramont.jpg": "Volkswagen Atlas Teramont front",
    "volvo_xc90.jpg": "Volvo XC90 front",
    "jaguar_ftype.jpg": "Jaguar F-Type coupe",
    "mini_cooper_jcw.jpg": "Mini Cooper John Cooper Works front",
    "peugeot_408.jpg": "Peugeot 408 2022 front",
    "isuzu_dmax.jpg": "Isuzu D-Max 2021 front",
    "suzuki_hayabusa.jpg": "Suzuki Hayabusa 2021",
    "royal_enfield_gt650.jpg": "Royal Enfield Continental GT 650",
    "mv_agusta_brutale.jpg": "MV Agusta Brutale 1000",
    "bmw_ce04.jpg": "BMW CE 04",
    "bmw_m4.jpg": "BMW M4 G82 front",
    "audi_r8.jpg": "Audi R8 4S front",
    "land_rover_defender.jpg": "Land Rover Defender L663 front"
}

HEADERS = {
    'User-Agent': 'DuyTruongMotorCatalog/2.0 (https://duytruong-motor.vercel.app/; truongduy24133009@gmail.com) Python-urllib'
}

def search_wikimedia_file(query):
    time.sleep(2.0)
    api_url = "https://commons.wikimedia.org/w/api.php?" + urllib.parse.urlencode({
        "action": "query",
        "format": "json",
        "list": "search",
        "srnamespace": "6",
        "srsearch": query,
        "srlimit": "8"
    })
    req = urllib.request.Request(api_url, headers=HEADERS)
    with urllib.request.urlopen(req, timeout=20) as resp:
        data = json.loads(resp.read().decode('utf-8'))
    results = data.get("query", {}).get("search", [])
    for res in results:
        title = res["title"]
        lower = title.lower()
        if lower.endswith(".jpg") or lower.endswith(".jpeg") or lower.endswith(".png"):
            if not any(bad in lower for bad in ["logo", "icon", "drawing", "badge", "emblem", "interior"]):
                return title
    return results[0]["title"] if results else None

def get_file_thumb_url(title):
    time.sleep(2.0)
    api_url = "https://commons.wikimedia.org/w/api.php?" + urllib.parse.urlencode({
        "action": "query",
        "format": "json",
        "titles": title,
        "prop": "imageinfo",
        "iiprop": "url",
        "iiurlwidth": "1200"
    })
    req = urllib.request.Request(api_url, headers=HEADERS)
    with urllib.request.urlopen(req, timeout=20) as resp:
        data = json.loads(resp.read().decode('utf-8'))
    pages = data.get("query", {}).get("pages", {})
    for pid, pdata in pages.items():
        info_list = pdata.get("imageinfo", [])
        if info_list:
            thumb = info_list[0].get("thumburl")
            if thumb:
                thumb = thumb.split('?')[0]
                thumb = thumb.replace('https://thumb.wikimedia.org/', 'https://upload.wikimedia.org/')
                return thumb
            return info_list[0].get("url")
    return None

frontend_dir = os.path.join("d:\\WEB", "frontend", "public", "images")
backend_dir = os.path.join("d:\\WEB", "src", "main", "resources", "static", "images")

os.makedirs(frontend_dir, exist_ok=True)
os.makedirs(backend_dir, exist_ok=True)

for filename, query in SEARCHES.items():
    frontend_path = os.path.join(frontend_dir, filename)
    backend_path = os.path.join(backend_dir, filename)
    
    if os.path.exists(frontend_path) and os.path.getsize(frontend_path) > 30000:
        print(f"[EXISTS] {filename} ({os.path.getsize(frontend_path)} bytes)")
        if not os.path.exists(backend_path) or os.path.getsize(backend_path) != os.path.getsize(frontend_path):
            shutil.copyfile(frontend_path, backend_path)
        continue
        
    print(f"[SEARCHING] {query} -> {filename}...")
    try:
        title = search_wikimedia_file(query)
        if not title:
            words = query.split()
            if len(words) > 2:
                title = search_wikimedia_file(words[0] + " " + words[1])
                
        if title:
            print(f"  Found Title: {title}")
            img_url = get_file_thumb_url(title)
            if img_url:
                print(f"  Downloading: {img_url}")
                time.sleep(2.0)
                req = urllib.request.Request(img_url, headers=HEADERS)
                with urllib.request.urlopen(req, timeout=25) as resp, open(frontend_path, 'wb') as f:
                    f.write(resp.read())
                shutil.copyfile(frontend_path, backend_path)
                print(f"  [SAVED] {filename} ({os.path.getsize(frontend_path)} bytes)")
            else:
                print(f"  [NO THUMB URL] for {title}")
        else:
            print(f"  [NO RESULTS] for {query}")
    except Exception as e:
        print(f"  [ERROR] {e}")

print("Done processing all vehicle images.")
