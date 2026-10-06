#!/usr/bin/env python3
"""Generates the "seated on a crossarm" models and blockstates for the top-mounted insulators.

Run from the repository root:

    python scripts/gen_insulator_seats.py

A crossarm sits flush against its pole, at the back of its block (BlockCrossarm), so an insulator
standing on one has to move back towards the pole to sit on the arm's centre line: SEAT_SHIFT, the
distance from the middle of a block to the middle of the arm. BlockInsulatorBase reports the way it
moves as the `seat` property (none, or the way to the pole), and TileEntityInsulatorBase moves the
wire point and box by the same amount.

The insulators' own models are hand-made JSON, rotated to each facing by the blockstate's `y`.
Forge keeps only one model state per variant, so the shift can't be a second, per-property
transform; instead this writes four shifted copies of each model (`<model>_seat_<n|e|s|w>`, shifted
in the model's own directions) and lists every facing x seat combination in the blockstate, picking
the copy whose shift, once the facing's rotation is applied, points the right way. Rerun it after
editing an insulator's model.
"""

import json
import os

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src', 'main', 'resources', 'assets', 'realgrid')
MODEL_DIR = os.path.join(ROOT, 'models', 'block')
BLOCKSTATE_DIR = os.path.join(ROOT, 'blockstates')

SEAT_SHIFT = 5          # pixels: block centre (8) to the arm's centre line (13, the arm is 10-16)

# The top-mounted insulators (InsulatorGeometry VISE_TOP, F_NECK, POST_TOP), by registry name.
TOP_INSULATORS = [
    'hendrix_vti_2core', 'hendrix_vti_3core', 'hendrix_vti_3core_small',
    'locke_insulator_2core_new', 'locke_insulator_2core_old', 'locke_insulator_3core_new', 'locke_insulator_3core_old',
    'large_maclean_porcelain_insulator_new', 'small_maclean_porcelain_insulator_new',
    'small_maclean_porcelain_insulator_new_2', 'large_maclean_porcelain_insulator_old',
    'small_maclean_porcelain_insulator_old',
    'maclean_pti_2core', 'maclean_pti_3core', 'maclean_pti_5core', 'maclean_pti_5core_2', 'maclean_pti_5core_3',
    'maclean_pti_6core',
]

# (dx, dz) per direction, in block coordinates (north is -z).
DIRS = {'north': (0, -1), 'east': (1, 0), 'south': (0, 1), 'west': (-1, 0)}
SHORT = {'north': 'n', 'east': 'e', 'south': 's', 'west': 'w'}


def rotate(d, y):
    """A blockstate 'y' turns the model clockwise seen from above: north becomes east at 90."""
    dx, dz = d
    for _ in range((y // 90) % 4):
        dx, dz = -dz, dx
    return dx, dz


def direction_of(d):
    return next(name for name, v in DIRS.items() if v == d)


def shifted(model, d):
    dx, dz = (v * SEAT_SHIFT for v in DIRS[d])
    out = json.loads(json.dumps(model))
    for e in out.get('elements', []):
        for k in ('from', 'to'):
            e[k] = [e[k][0] + dx, e[k][1], e[k][2] + dz]
        if 'rotation' in e and 'origin' in e['rotation']:
            o = e['rotation']['origin']
            e['rotation']['origin'] = [o[0] + dx, o[1], o[2] + dz]
    return out


def main():
    for reg in TOP_INSULATORS:
        bs_path = os.path.join(BLOCKSTATE_DIR, reg + '.json')
        bs = json.load(open(bs_path, encoding='utf-8'))
        base = bs['defaults']['custom']['base']                 # e.g. realgrid:block/hendrix_vti_2core
        name = base.split(':')[1].split('/', 1)[1]
        model = json.load(open(os.path.join(MODEL_DIR, name + '.json'), encoding='utf-8'))
        for d in DIRS:
            with open(os.path.join(MODEL_DIR, '%s_seat_%s.json' % (name, SHORT[d])), 'w', newline='\n') as f:
                json.dump(shifted(model, d), f, indent=2)
                f.write('\n')

        facings = bs['variants'].get('facing') or {}
        if not facings:                                          # already in the combined form
            facings = {f: {'y': v['y']} for f, v in ((k.split(',')[0].split('=')[1], v)
                       for k, v in bs['variants'].items() if k.startswith('facing=') and 'seat=none' in k)}
        variants = {}
        for facing, fv in facings.items():
            y = fv.get('y', 0)
            for seat in ['none'] + list(DIRS):
                if seat == 'none':
                    b = base
                else:
                    in_model = direction_of(rotate(DIRS[seat], -y % 360))
                    b = '%s_seat_%s' % (base, SHORT[in_model])
                variants['facing=%s,seat=%s' % (facing, seat)] = {
                    'model': bs['defaults']['model'], 'custom': {'base': b}, 'y': y}
        variants['inventory'] = bs['variants']['inventory']
        bs['variants'] = variants
        with open(bs_path, 'w', newline='\n') as f:
            json.dump(bs, f, indent=2)
            f.write('\n')


if __name__ == '__main__':
    main()
