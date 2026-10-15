from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle

OUT = r'C:\Users\Marcos Eduardo\Documents\InteliJJ\Velune\output\pdf\Marcos-Oliveira-CV-FR.pdf'
pdfmetrics.registerFont(TTFont('Arial', r'C:\Windows\Fonts\arial.ttf'))
pdfmetrics.registerFont(TTFont('Arial-Bold', r'C:\Windows\Fonts\arialbd.ttf'))
pdfmetrics.registerFontFamily('Arial', normal='Arial', bold='Arial-Bold', italic='Arial', boldItalic='Arial-Bold')

NAVY = colors.HexColor('#18324E')
TEXT = colors.HexColor('#202830')
MUTED = colors.HexColor('#4C5965')
RULE = colors.HexColor('#18324E')

name = ParagraphStyle('Name', fontName='Arial-Bold', fontSize=20, leading=22, textColor=NAVY, alignment=TA_CENTER, spaceAfter=2)
contact = ParagraphStyle('Contact', fontName='Arial-Bold', fontSize=9.6, leading=12, textColor=TEXT, alignment=TA_CENTER)
section = ParagraphStyle('Section', fontName='Arial-Bold', fontSize=10.5, leading=12.2, textColor=NAVY, spaceBefore=5, spaceAfter=3)
body = ParagraphStyle('Body', fontName='Arial', fontSize=9.3, leading=11.4, textColor=TEXT, spaceAfter=1.8)
bullet = ParagraphStyle('Bullet', parent=body, leftIndent=9, firstLineIndent=-7, spaceAfter=1.15)
job = ParagraphStyle('Job', fontName='Arial-Bold', fontSize=9.3, leading=11.4, textColor=NAVY, spaceAfter=1.6)

class CVDoc(SimpleDocTemplate):
    def afterPage(self):
        c = self.canv
        w, h = A4
        c.saveState()
        c.setStrokeColor(colors.HexColor('#D2D9E0'))
        c.setLineWidth(0.45)
        c.line(17*mm, 12*mm, w-17*mm, 12*mm)
        c.restoreState()

doc = CVDoc(OUT, pagesize=A4, leftMargin=17*mm, rightMargin=17*mm, topMargin=13*mm, bottomMargin=15*mm,
            title='CV - Marcos Eduardo Oliveira Silva', author='Marcos Eduardo Oliveira Silva')
story = [
    Paragraph('Marcos Eduardo Oliveira Silva', name),
    Paragraph('E-mail : marcos_ptc1009@hotmail.com  |  Téléphone : +33 07 67 96 23 81', contact),
    Paragraph('Paris, France (télétravail)', contact),
    Spacer(1, 2.5),
]

def heading(text):
    story.append(Paragraph(text, section))
    rule = Table([['']], colWidths=[A4[0]-34*mm], rowHeights=[0.45])
    rule.setStyle(TableStyle([('LINEBELOW', (0,0), (-1,-1), 0.45, RULE)]))
    story.append(rule)
    story.append(Spacer(1, 2))

def para(text, style=body):
    story.append(Paragraph(text, style))

def bullet_item(text):
    para('&bull;&nbsp; ' + text, bullet)

heading('PROFIL PROFESSIONNEL')
para("Professionnel trilingue ayant une expérience de l’accueil et du service client dans l’hôtellerie à Paris. Habitué à accompagner une clientèle internationale, à adapter sa communication et à traiter les demandes avec écoute, précision et respect des consignes. Locuteur natif du portugais brésilien, courant en anglais et en français, avec un intérêt pour l’évaluation de la qualité linguistique et de la pertinence des réponses d’intelligence artificielle. Suit actuellement le programme CS50x de Harvard.")

heading('COMPÉTENCES PRINCIPALES')
para("Accueil et accompagnement d’une clientèle internationale • Écoute active et traitement des demandes • Adaptation de la communication aux besoins des clients • Service multilingue (portugais PT-BR, anglais, français) • Communication claire, orale et écrite • Vérification des informations et attention aux détails • Respect des procédures et des standards de qualité • Sens du service et suivi des demandes • Travail autonome et collaboration à distance")

heading('COMPÉTENCES')
for item in [
    "<b>Approche d’évaluation de l’IA :</b> utilisation quotidienne et pratique d’assistants d’IA ; sensibilité aux inférences incorrectes, aux rapprochements forcés et aux affirmations non étayées.",
    "<b>Qualité de la personnalisation :</b> évaluer si les réponses utilisent le contexte de l’utilisateur de manière appropriée et naturelle, sans narration excessivement robotique.",
    "<b>Justifications écrites :</b> explications claires, concises et structurées des comparaisons et classements.",
    "<b>Portugais natif (PT-BR) :</b> grammaire, cohésion, registre, variations régionales, ton formel et informel.",
    "<b>Langues :</b> portugais (natif), anglais (courant), français (courant).",
    "<b>Conception de prompts :</b> création de prompts variés et de conversations à plusieurs tours pour tester le comportement des modèles.",
    "<b>Compétences techniques :</b> CS50x (C, Python, SQL, algorithmes, structures de données) ; apprentissage actuel de Java pour le développement backend.",
    "<b>Méthode de travail :</b> autonome, régulier, à l’aise avec les outils web en environnement distant.",
]: bullet_item(item)

heading('EXPÉRIENCE PROFESSIONNELLE')
para('Accueil et service client — Hôtel Balzac, Paris, France | avril 2026 – septembre 2026', job)
for item in [
    "Adaptation du ton, du vocabulaire et du niveau de formalité aux clients de pays et de profils différents, avec une communication en portugais, anglais et français.",
    "Adaptation des réponses au contexte et aux besoins de chaque client, en évitant les réponses génériques, conformément aux critères d’évaluation de la personnalisation.",
    "Application des consignes et des scripts qualité de l’entreprise, avec un traitement précis des demandes dans un environnement dynamique.",
    "Vérification des informations des clients afin de prévenir les erreurs et les incohérences.",
]: bullet_item(item)

heading('FORMATION')
bullet_item('CS50x : Introduction à l’informatique, Harvard University (edX) — en cours')
bullet_item('Diplôme de fin d’études secondaires, Escola Estadual Dom Lustosa, Brésil')

heading('INFORMATIONS COMPLÉMENTAIRES')
for item in [
    "À l’aise dans les échanges avec une clientèle internationale et dans l’adaptation de la communication aux besoins de chaque interlocuteur.",
    "Ordinateur portable personnel et connexion Internet stable, adaptés au travail à distance.",
]: bullet_item(item)

doc.build(story)
print(OUT)
