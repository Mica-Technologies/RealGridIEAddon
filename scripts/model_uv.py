"""Explicit face UVs for generated JSON model elements.

Minecraft gives an element without `uv` the UVs of its own position in the block. An element that
reaches outside the block (a railing standing up into the block above, a bolt through a neighbour)
then gets UVs outside 0..16, which sample whatever sprite lies next to its texture on the atlas.
These keep every face's UVs inside its sprite, shifted by whole blocks so the pattern still lines up.
"""


def _fit(a, b):
    if b - a >= 16:
        return 0.0, 16.0
    shift = (a // 16) * 16
    a, b = a - shift, b - shift
    if b > 16:
        a, b = a - (b - 16), 16.0
    return round(a, 4), round(b, 4)


def faces(lo, hi, texture):
    """@return the six faces of the box lo..hi (pixels), each with its texture and in-sprite UVs"""
    x0, y0, z0 = lo
    x1, y1, z1 = hi
    spans = {
        'north': ((16 - x1, 16 - x0), (16 - y1, 16 - y0)),
        'south': ((x0, x1), (16 - y1, 16 - y0)),
        'west': ((z0, z1), (16 - y1, 16 - y0)),
        'east': ((16 - z1, 16 - z0), (16 - y1, 16 - y0)),
        'up': ((x0, x1), (z0, z1)),
        'down': ((x0, x1), (16 - z1, 16 - z0)),
    }
    out = {}
    for face, (u, v) in spans.items():
        u0, u1 = _fit(*u)
        v0, v1 = _fit(*v)
        out[face] = {'texture': texture, 'uv': [u0, v0, u1, v1]}
    return out


def png_rgb(path, pixels, width, height):
    """Writes an RGB PNG of any size (gen_pole_light_mounts.png is 16 px square only)."""
    import struct
    import zlib
    raw = b''.join(b'\x00' + bytes(v for px in pixels[y * width:(y + 1) * width] for v in px) for y in range(height))

    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)

    with open(path, 'wb') as f:
        f.write(b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0))
                + chunk(b'IDAT', zlib.compress(raw, 9)) + chunk(b'IEND', b''))
