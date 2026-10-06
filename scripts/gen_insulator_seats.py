#!/usr/bin/env python3
"""Generates the "on a crossarm" models and blockstates for the insulators and cutoff switches.

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

Blocks that hang from their back (side-mount and dead-end insulators, cutoff switches) are fixed to
an arm's front face, back face or an end instead: the `arm` property (ArmSeat in Java), worked out
from the block behind them. Their moves are fixed in the block's own facing frame (closing the
10 px gap to an arm's front face, or 5 px sideways to an end's centre line, and lifting the wire
point to the arm's centre line), so each model needs only one copy per kind,
`<model>_arm_<front|back|end_left|end_right>`, whatever the facing. Which models these are is
read from the Java sources (InsulatorGeometry presets, the cutoff blocks), and the lifts must match
InsulatorGeometry and BlockCutoffSwitchBase.ARM_LIFT_PX.
"""

import glob
import json
import os
import re

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

JAVA = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src', 'main', 'java', 'com', 'micatechnologies',
                    'realgrid', 'blocks')

# Pixels each kind of back-hung block rises on an arm (InsulatorGeometry / BlockCutoffSwitchBase).
LIFTS = {'SIDE_MOUNT': 4, 'DEAD_END': 2, 'CUTOFF': 2}
FRONT_GAP = 10          # pixels from an arm block's front to the arm's front face
END_SHIFT = 5           # pixels from a block's centre to the arm's centre line
ARM_KINDS = ('front', 'back', 'end_left', 'end_right')

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


def cw(d):
    return -d[1], d[0]


def ccw(d):
    return d[1], -d[0]


def back_hung():
    """(registry name, lift) of every side-mount and dead-end insulator and every cutoff switch."""
    out = []
    for te in sorted(glob.glob(os.path.join(JAVA, 'insulators', 'TileEntity*.java'))):
        m = re.search(r'InsulatorGeometry\.(DEAD_END|SIDE_MOUNT)', open(te, encoding='utf-8').read())
        if not m:
            continue
        block = os.path.join(os.path.dirname(te), 'Block' + os.path.basename(te)[len('TileEntity'):])
        reg = re.search(r'super\("([^"]+)"', open(block, encoding='utf-8').read())
        if reg:
            out.append((reg.group(1), LIFTS[m.group(1)]))
    for block in sorted(glob.glob(os.path.join(JAVA, 'cutoffs', 'BlockCutoffSwitch[0-9]*.java')) +
                        glob.glob(os.path.join(JAVA, 'cutoffs', 'BlockCutoffSwitch.java'))):
        reg = re.search(r'super\("([^"]+)"', open(block, encoding='utf-8').read())
        if reg:
            out.append((reg.group(1), LIFTS['CUTOFF']))
    return out


def moved(model, dx, dy, dz):
    out = json.loads(json.dumps(model))
    for e in out.get('elements', []):
        for k in ('from', 'to'):
            e[k] = [e[k][0] + dx, e[k][1] + dy, e[k][2] + dz]
        if 'rotation' in e and 'origin' in e['rotation']:
            o = e['rotation']['origin']
            e['rotation']['origin'] = [o[0] + dx, o[1] + dy, o[2] + dz]
    return out


def arm_offsets(y_north, lift):
    """Model-space (dx, dy, dz) per arm kind, for a model the blockstate turns by y_north to face north."""
    fwd = rotate(DIRS['north'], -y_north % 360)
    left, right = ccw(fwd), cw(fwd)
    return {
        'front': (-fwd[0] * FRONT_GAP, lift, -fwd[1] * FRONT_GAP),
        'back': (0, lift, 0),
        'end_left': (left[0] * END_SHIFT, lift, left[1] * END_SHIFT),
        'end_right': (right[0] * END_SHIFT, lift, right[1] * END_SHIFT),
    }


def model_path(base):
    return os.path.join(MODEL_DIR, base.split(':')[1].split('/', 1)[1] + '.json')


def back_hung_blockstate(reg, lift):
    bs_path = os.path.join(BLOCKSTATE_DIR, reg + '.json')
    bs = json.load(open(bs_path, encoding='utf-8'))
    v = bs['variants']
    if 'facing' in v:                                            # first run: the hand-written form
        ys = {f: fv.get('y', 0) for f, fv in v['facing'].items()}
        actives = {a: av['custom']['base'] for a, av in v['active'].items()} if 'active' in v else None
    else:                                                         # rerun: read the combined form back
        ys, actives = {}, {}
        for k, val in v.items():
            if k == 'inventory' or 'arm=none' not in k:
                continue
            props = dict(kv.split('=') for kv in k.split(','))
            ys[props['facing']] = val.get('y', 0)
            if 'active' in props:
                actives[props['active']] = val['custom']['base']
        actives = actives or None
    default_base = bs['defaults']['custom']['base']
    bases = set(actives.values()) if actives else {default_base}
    offsets = arm_offsets(ys['north'], lift)
    for base in bases:
        model = json.load(open(model_path(base), encoding='utf-8'))
        for kind, off in offsets.items():
            with open(model_path(base)[:-5] + '_arm_%s.json' % kind, 'w', newline='\n') as f:
                json.dump(moved(model, *off), f, indent=2)
                f.write('\n')
    variants = {}
    for facing, y in ys.items():
        for kind in ('none',) + ARM_KINDS:
            for active, base in (actives.items() if actives else [(None, default_base)]):
                b = base if kind == 'none' else '%s_arm_%s' % (base, kind)
                key = 'active=%s,arm=%s,facing=%s' % (active, kind, facing) if active else 'arm=%s,facing=%s' % (kind, facing)
                variants[key] = {'model': bs['defaults']['model'], 'custom': {'base': b}, 'y': y}
    variants['inventory'] = v['inventory']
    bs['variants'] = variants
    with open(bs_path, 'w', newline='\n') as f:
        json.dump(bs, f, indent=2)
        f.write('\n')


def main():
    for reg, lift in back_hung():
        back_hung_blockstate(reg, lift)
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
