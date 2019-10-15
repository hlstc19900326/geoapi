from abc import ABC, property, abstractmethod
from enum import Enum


class AxisDirection(Enum):
    """
    The direction of positive increase in the coordinate value for a coordinate system axis. This direction is exact in
    some cases, and is approximate in other cases.
    """
    COLUMN_NEGATIVE = "columnNegative"
    COLUMN_POSITIVE = "columnPositive"
    DISPLAY_DOWN = "displayDown"
    DISPLAY_LEFT = "displayLeft"
    DISPLAY_RIGHT = "displayRight"
    DISPLAY_UP = "displayUp"
    DOWN = "down"
    EAST = "east"
    EAST_NORTH_EAST = "eastNorthEast"
    EAST_SOUTH_EAST = "eastSouthEast"
    FUTURE = "future"
    GEOCENTRIC_X = "geocentricX"
    GEOCENTRIC_Y = "geocentricY"
    GEOCENTRIC_Z = "geocentricZ"
    NORTH = "north"
    NORTH_EAST = "northEast"
    NORTH_NORTH_EAST = "northNorthEast"
    NORTH_NORTH_WEST = "northNorthWest"
    NORTH_WEST = "northWest"
    OTHER = "other"
    PAST = "past"
    ROW_NEGATIVE = "rowNegative"
    ROW_POSITIVE = "rowPositive"
    SOUTH = "south"
    SOUTH_EAST = "southEast"
    SOUTH_SOUTH_EAST = "southSouthWest"
    SOUTH_SOUTH_WEST = "southSouthWest"
    SOUTH_WEST = "southWest"
    UP = "up"
    WEST = "west"
    WEST_NORTH_WEST = "westNorthWest"
    WEST_SOUTH_WEST = "westSouthWest"


class RangeMeaning(Enum):
    """
    Meaning of the axis value range specified through minimum value and maximum value.
    """
    EXACT = "exact"
    WRAPAROUND = "wraparound"


class IdentifiedObject(ABC):
    """
    Identification and remarks information for a reference system or CRS-related object.
    """

    @property
    @abstractmethod
    def name(self) -> str:
        """
        The primary name by which this object is identified.
        """
        pass

    @property
    def remarks(self) -> str:
        """
        Comments on or information about this object, including data source information.
        """
        return None

    @property
    @abstractmethod
    def to_wkt(self) -> str:
        """
        Returns a Well-Known Text (WKT) for this object.
        """
        pass


class CoordinateSystemAxis(IdentifiedObject):
    """
    Definition of a coordinate system axis.
    """

    @property
    @abstractmethod
    def abbreviation(self) -> str:
        """
        Returns the abbreviation used for this coordinate system axes.
        """
        pass

    @property
    @abstractmethod
    def direction(self) -> AxisDirection:
        """
        Returns the direction of this coordinate system axis.
        """
        pass

    @property
    @abstractmethod
    def unit(self):
        """
        Returns the unit of measure used for this coordinate system axis.
        """
        pass

    @property
    def minimum_value(self) -> float:
        """
        Returns the minimum value normally allowed for this axis, in the unit of measure for the axis.
        """
        return float("-inf")

    @property
    def maximum_value(self) -> float:
        """
        Returns the maximum value normally allowed for this axis, in the unit of measure for the axis. If there is no
        maximum value, then this method returns positive infinity.
        """
        return float("inf")

    @property
    def range_meaning(self) -> RangeMeaning:
        """
        Returns the meaning of axis value range specified by the minimum and maximum values.
        """
        return None


class CoordinateSystem(IdentifiedObject):
    """
    The set of coordinate system axes that spans a given coordinate space. A coordinate system (CS) is derived from a
    set of (mathematical) rules for specifying how coordinates in a given space are to be assigned to points. The
    coordinate values in a coordinate tuple shall be recorded in the order in which the coordinate system axes
    associations are recorded, whenever those coordinates use a coordinate reference system that uses this coordinate system.
    """

    @property
    @abstractmethod
    def dimension(self) -> int:
        """
        Returns the dimension of the coordinate system.
        """
        pass

    @property
    @abstractmethod
    def axis(self) -> CoordinateSystemAxis:
        """
        Returns the axis for this coordinate system at the specified dimension.
        """
        pass


class AffineCS(CoordinateSystem):
    """
    A 2- or 3-dimensional coordinate system with straight axes that are not necessarily orthogonal.
    """


class CartesianCS(CoordinateSystem):
    """
    A 2- or 3-dimensional coordinate system with orthogonal straight axes. All axes shall have the same length unit of
    measure.
    """


class CylindralCS(CoordinateSystem):
    """
    A 3-dimensional coordinate system consisting of a PolarCS extended by a straight axis perpendicular to the plane
    spanned by the polar CS.
    """


class EllipsoidalCS(CoordinateSystem):
    """
    A 2- or 3-dimensional coordinate system in which position is specified by geodetic latitude, geodetic longitude,
    and (in the 3D case) ellipsoidal height.
    """


class LinearCS(CoordinateSystem):
    """
    A 1-dimensional coordinate system that consists of the points that lie on the single axis described. The associated
    coordinate is the distance – with or without offset – from the origin point, specified through the datum definition, to the point along the axis.
    """


class ParametricCS(CoordinateSystem):
    """
    A 1-dimensional coordinate system containing a single axis. This coordinate system uses parameter values or
    functions to describe the position of a point.
    """


class PolarCS(CoordinateSystem):
    """
    A 2-dimensional coordinate system in which position is specified by the distance from the origin and the angle
    between the line from the origin to a point and a reference direction.
    """


class SphericalCS(CoordinateSystem):
    """
    A 3-dimensional coordinate system with one distance measured from the origin and two angular coordinates. Not to be
    confused with an EllipsoidalCS based on an ellipsoid "degenerated" into a sphere.
    """


class TimeCS(CoordinateSystem):
    """
    A 1-dimensional coordinate system containing a single time axis. This coordinate system is used to describe the
    temporal position of a point in the specified time units from a specified time origin.
    """


class VerticalCS(CoordinateSystem):
    """
    A 1-dimensional coordinate system used to record the heights or depths of points. Such a coordinate system is
    usually dependent on the Earth's gravity field, perhaps loosely as when atmospheric pressure is the basis for the
    vertical coordinate system axis. An exact definition is deliberately not provided as the complexities of the subject
    fall outside the scope of the ISO 19111 specification.
    """
