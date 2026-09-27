
UV:
(0,0) ------------------ (1,0)
|                        |
|                        |
|                        |
|                        |
|                        |
|                        |
|                        |
(0,1) ------------------ (1,1)

QUBE:
(0,16) ---------------- (16,16)
  |                        |
  |                        |
  |                        |
  |                        |
  |                        |
  |                        |
  |                        |
(0,0) ------------------ (16,0)


1) Lower left
2) Upper left
3) Lower right
4) Upper right


U maps 0 to 0 and 16 to 1 -> Normal lerping between quadUvs[0] and quadUvs[4]
V maps 16 to 0 and 0 to 1 -> Inverse lerping between quadUvs[1] and quadUvs[3]

Resulting in the following UVs:
0,1,0,0,1,1,1,0



1) Find center of Quad (via position of vertices)
2) 